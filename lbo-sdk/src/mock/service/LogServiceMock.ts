import { LogLevel } from "@/core/enum/LogLevel"
import LogServiceInterface from "@/core/interface/service/LogServiceInterface"

export default class LogServiceMock implements LogServiceInterface {
  private logLevel = LogLevel.All

  private get preventLog(): boolean {
    return this.logLevel === LogLevel.None
  }

  private get noError(): boolean {
    return this.preventLog || this.logLevel < 1
  }
  
  private get noWarn(): boolean {
    return this.preventLog || this.logLevel < 2
  }

  private get noLog(): boolean {
    return this.preventLog || this.logLevel < 3
  }

  public get currentLogLevel(): LogLevel {
    return this.logLevel
  }

  public setLogLevel(logLevel: LogLevel) {
    this.logLevel = logLevel
  }

  public log(...data: any[]) {
    if (this.noLog) {
      return
    }
    console.log(...data)
  }

  public warn(...data: any[]) {
    if (this.noWarn) {
      return
    }
    console.warn(...data)
  }

  public error(...data: any[]) {
    if (this.noError) {
      return
    }
    console.error(...data)
  }
}
