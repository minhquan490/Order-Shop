import { map, mergeMap, of, switchMap } from 'rxjs';
import { ChunkFileUploadService, FileUploadServiceInitializer } from "~/services/chunk-file-upload.service";
import { HttpService, HttpServiceProvider } from "~/services/http.service";
import { UploadFileResult } from "~/types/file-upload-result.type";
import { Resource } from "~/types/resource-upload.type";

export class DefaultChunkFileUploadService implements ChunkFileUploadService, FileUploadServiceInitializer {
    private uploadService!: HttpService;
    private flushService!: HttpService;

    constructor(private httpClient: HttpServiceProvider) {}

    init(uploadUrl: string, flushUrl: string): ChunkFileUploadService {
        this.uploadService = this.httpClient.open(uploadUrl);
        this.flushService = this.httpClient.open(flushUrl);
        return this;
    }

    uploadFile(file: File): UploadFileResult {
        let isError = false;
        let messages: Array<string> = [];
        const reader = new FileReader();
        of(file)
            .pipe(switchMap(file => this.splitFile(file)))
            .pipe(map((chunk, index) => this.convertChunk(chunk, index, reader, file)))
            .pipe(map((callback) => callback()))
            .pipe(mergeMap(async (resource) => this.uploadService.post<Resource, { status: number; message: string; }>(resource)))
            .subscribe((response) => {
                if (response.status < 400) {
                    isError = this.sendFlush(file, messages);
                } else {
                    isError = true;
                    messages.push(response.message);
                }
            });
        return new UploadFileResult(isError, messages);
    }

    private convertChunk(chunk: Blob, index: number, reader: FileReader, file: File): () => Resource {
        reader.readAsDataURL(chunk);
        return reader.onloadend = () => {
            return {
                data: (reader.result) ? reader.result.toString() : '',
                file_name: file.name.replace('.', `-${index}.`),
                total_size: chunk.size
            } as Resource;
        }
    }

    private splitFile(file: File): Array<Blob> {
        const maxChunk = 1024 * 1024 * 500;
        const fileSize = file.size;
        const chunks: Array<Blob> = [];
        for (let index = 0; index < fileSize / maxChunk; index++) {
            const blob = file.slice(index * maxChunk, maxChunk + maxChunk * index);
            chunks.push(blob);
        }
        return chunks;
    }

    private sendFlush(file: File, messages: Array<string>): boolean {
        const names: Array<string> = file.name.split('-');
        const res = this.flushService.post<
            {file_id: string, product_id: string, content_type: string}, 
            {status: number, message: string}
        >({file_id: names[1], product_id: names[0], content_type: file.type});
        messages.push(res.message);
        return res.status >= 400;
    }
}