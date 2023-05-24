import { map, mergeMap, of, switchMap } from 'rxjs';
import { ChunkFileUploadService } from "~/services/chunk-file-upload-service";
import { HttpServiceProvider } from "~/services/http-service";
import { UploadFileResult } from "~/types/file-upload-result";
import { Resource } from "~/types/resource-upload";

export class DefaultChunkFileUploadService extends ChunkFileUploadService {
    private httpClient: HttpServiceProvider;

    constructor(httpClient: HttpServiceProvider) {
        super();
        this.httpClient = httpClient;
    }

    override uploadFile(file: File): UploadFileResult {
        const service = this.httpClient.open(process.env.FILE_UPLOAD_URL as string);
        let isError = false;
        let messages: Array<string> = [];
        const reader = new FileReader();
        of(file)
            .pipe(switchMap(file => this.splitFile(file)))
            .pipe(map((chunk, index) => this.convertChunk(chunk, index, reader, file)))
            .pipe(map((callback) => callback()))
            .pipe(mergeMap(async (resource) => service.post<Resource, { status: number; message: string; }>(resource)))
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
        const flushService = this.httpClient.open(process.env.FILE_FLUSH_URL as string);
        const names: Array<string> = file.name.split('-');
        const res = flushService.post<
            {file_id: string, product_id: string, content_type: string}, 
            {status: number, message: string}
        >({file_id: names[1], product_id: names[0], content_type: file.type});
        messages.push(res.message);
        return res.status >= 400;
    }
}