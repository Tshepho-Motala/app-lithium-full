import { parseISO } from 'date-fns'
import {format} from 'date-fns-tz'

/**
 * computes a datetime value using format yyyy-MM-dd HH:mm:ss
 * @param date 
 * @returns 
 */
export const toSimpleDateTimeFormat = (date: Date): string => format(date, 'yyyy-MM-dd HH:mm:ss')

/**
 * computes a date value using format yyyy-MM-dd
 * @param date 
 * @returns string
 */
export const toSimpleDateFormat = (date: Date): string => format(date, 'yyyy-MM-dd')

/**
 * computes a time value using format HH:mm
 * @param date 
 * @returns string
 */
export const toSimpleTimeFormat = (date: Date): string => format(date, 'HH:mm')

/**
 * computes a time value using the provided format
 * @param date 
 * @param dateTimeFormat
 * @returns string
 */
export const toDateTimeFormat = (date: Date, dateTimeFormat: string): string => format(date, dateTimeFormat)


export const toFormatForZone = (date: Date | string, formatStr: string, timeZone: string): string => {
    if(typeof date === 'string') {
        date = date.replace(".000Z", "")
        date = parseISO(date)
    }
    return format(date, formatStr, { timeZone })
}


export const utcDateStringToZonedDateTime = (date: string, timezone: string) => new Date(date)

export const toSimpleTimeFormatForZone = (date: Date | string, timeZone: string): string => toFormatForZone(date, 'HH:mm', timeZone)
export const toSimpleDateFormatForZone = (date: Date | string, timeZone: string): string => toFormatForZone(date, 'yyyy-MM-d', timeZone)
