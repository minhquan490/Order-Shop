import { UploadFileResult } from "~/types/file-upload-result.type";

export abstract class ChunkFileUploadService {
    abstract uploadFile(file: File): UploadFileResult;
}