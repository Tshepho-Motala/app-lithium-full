export class HotText {
  _value: { [key: string]: any } = {}

  _withValue = ''

  title = ''

  get placeholder() {
    return this._withValue.replace(/%\w+%/g, '[X]')
  }

  get keys() {
    const keys = this._withValue.match(/%\w+%/g)
    if (keys === null) {
      return []
    }
    return keys.map((s) => s.slice(1, -1))
  }

  constructor(withValue: string) {
    this._withValue = withValue
  }

  get hasValueKeys() {
    const keys = Object.keys(this._value)
    return keys.length > 0
  }

  get display() {
    if (this.hasValueKeys) {
      return this._withValue.replace(/%\w+%/g, (all) => {
        return this._value[all] || all
      })
    }

    return this.placeholder
  }

  get html() {
    if (this.hasValueKeys) {
      return this._withValue.replace(/%\w+%/g, (all) => {
        return '<span class="primary--text">' + (this._value[all] || all) + '</span>'
      })
    }

    return this.placeholder
  }

  setValue(key: string, value: any) {
    this._value['%' + key + '%'] = value
  }
}

export class HotTextDefault {
  _ht: HotText | null = null
  _noValueText: string

  constructor(noValueText: string, ht?: HotText | undefined) {
    this._noValueText = noValueText
    this._ht = ht || null
  }

  get html() {
    if (this._ht === null || !this._ht.hasValueKeys) {
      return `<span class="grey--text text--darken-1">${this._noValueText}</span>`
    }
    return this._ht.html
  }

  set(ht: HotText | null) {
    this._ht = ht
  }

  setValue(key: any, v: any) {
    if (this._ht) {
      this._ht.setValue(key, v)
    }
  }

  clear() {
    this._ht = null
  }
}
