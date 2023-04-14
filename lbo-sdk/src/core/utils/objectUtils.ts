export const isDefined = (obj: any): boolean => typeof obj !== 'undefined'

export const isNull = (obj: any): boolean => !isDefined(obj) || null === obj

export const isEmpty = (obj: any): boolean => isNull(obj) || Object.keys(obj).length === 0 && obj.constructor === Object
