class HttpCommon {
  encodedContentType = 'application/x-www-form-urlencoded;charset=UTF-8'

  /**
   * Build a valid Headers object with optional custom header fields
   *
   * https://stackoverflow.com/a/3561399
   *
   * @returns Headers
   */
  public async getHeaders(customHeaders?: Map<string, string> | undefined) {
    const headers = new Headers()
    headers.set('Content-Type', this.encodedContentType)

    if (customHeaders) {
      customHeaders.forEach((value, name) => {
        headers.set(name, value)
      })
    }

    return headers
  }

  public getCookieValue(cookieName: string) {
    const b: RegExpMatchArray | null = document.cookie.match(
      '(^|;)\\s*' + cookieName + '\\s*=\\s*([^;]+)'
    )
    return (b ? b.pop() : '') || ''
  }
}

export default new HttpCommon()
