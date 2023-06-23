import { ChunkFileUploadService, FileUploadServiceInitializer } from "~/services/chunk-file-upload.service";

export const useFileUpload = () => {
  const fileUploadService: FileUploadServiceInitializer = inject('fileUploadServiceInitializer') as FileUploadServiceInitializer;

  function initService(uploadUrl: string, flushUrl: string): ChunkFileUploadService {
    return fileUploadService.init(uploadUrl, flushUrl);
  }

  return ref(initService);
}
