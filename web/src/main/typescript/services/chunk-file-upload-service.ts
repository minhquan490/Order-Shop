import { UploadFileResult } from "~/types/upload-file-result";

export abstract class ChunkFileUploadService {
    abstract uploadFile(file: File): UploadFileResult;
}