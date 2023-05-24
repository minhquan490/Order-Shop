import { ChunkFileUploadService } from "~/services/chunk-file-upload-service";
import { UploadFileResult } from "~/types/file-upload-result";

export const useFileUpload = () => {
  const fileUploadService: ChunkFileUploadService = inject('fileUploadService') as ChunkFileUploadService;

  function upload(file: File): UploadFileResult {
    return fileUploadService.uploadFile(file);
  }

  return { upload };
}
