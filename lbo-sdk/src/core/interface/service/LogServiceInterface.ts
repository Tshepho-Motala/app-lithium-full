import { LogLevel } from "../../enum/LogLevel";

export default interface LogServiceInterface {
  currentLogLevel: LogLevel
  setLogLevel(logLevel: LogLevel): void

  log(...data: any[]): void
  warn(...data: any[]): void
  error(...data: any[]): void
}
