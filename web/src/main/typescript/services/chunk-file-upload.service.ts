import { UploadFileResult } from "~/types/file-upload-result.type";

export abstract class ChunkFileUploadService {
    abstract uploadFile(file: File): UploadFileResult;
}

export abstract class FileUploadServiceInitializer {
    abstract init(uploadUrl: string, flushUrl: string): ChunkFileUploadService;
}