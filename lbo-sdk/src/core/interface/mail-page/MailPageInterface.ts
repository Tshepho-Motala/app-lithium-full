export interface TemplateListItemInterface {
    current: TemplateListItemBodyInterface,
    name: string,
    id: number,
    [key: string]: any
}
export interface TemplateListItemBodyInterface {
    body: string,
    emailFrom: string,
    subject: string,
    id: number,
    [key: string]: any
}
export interface TemplateListPlaceholderInterface {
    key: string,
    value: string
}
