export default interface CsvGeneratorProvider {
  generate(config: ExportConfig): Promise<GenerateResponse>
  progress(config: ExportConfig): Promise<ExportProgress>
  download(reference: number): Promise<void>

  cancelGeneration(reference: number): Promise<void>

  getConfig(): ExportConfig
}

export enum ExportStatus {
  CREATED = 'CREATED',
  BUSY = 'BUSY',
  COMPLETE = 'COMPLETE',
  FAILED = 'FAILED',
  DOWNLOADED = 'DOWNLOADED',
  IDLE = 'IDLE',
  CANCELED = 'CANCELED'
}

export interface GenerateResponse {
  reference: number
  status: ExportStatus
  comment: string
}

export interface ExportProgress {
  status: ExportStatus
  comment: string
}

export interface ExportConfig {
  provider: string
  domain: string
  role: string
  reference: number
}
