export  interface  getAllRulesetsApiParams {
    draw: Number,
    start: Number,
    length: Number,
    domains: String,
    'search[value]'?: String | null,
    lastUpdatedStart?: String | null,
    lastUpdatedEnd?: String | null,
    enabled?: String | null,
    name?: String | null,
    [key: string]: any
}
export  interface sendExportApiApiParams{
    ids: string
}