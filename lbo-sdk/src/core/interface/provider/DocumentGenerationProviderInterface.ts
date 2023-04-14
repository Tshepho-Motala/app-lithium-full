import { DocumentTypeInterface } from '@/mock/provider/DocumentGenerationMock'

export default interface DocumentGenerationProviderInterface {
  generateCsv(): Promise<void>

  data: any
  loadDocumentTypes(): Promise<any>
  saveDocumentType(documentType: any): Promise<void>

  loadDocuments(): Promise<any>
  loadAvailableDocumentTypes(internalOnly: boolean): Promise<DocumentTypeInterface[]>
  loadAvailableReviewReasons(): Promise<any>
  updateDocument(document: any): Promise<void>
  uploadDocument(file: any, document: any): Promise<any>
  deleteDocument(documentId: any): Promise<void>
  loadDocumentFile(documentFileId: any): Promise<any>

  closeUploadDialog(): void
}
