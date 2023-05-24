import { UploadFileResult } from "~/types/file-upload-result";

export abstract class ChunkFileUploadService {
    abstract uploadFile(file: File): UploadFileResult;
}