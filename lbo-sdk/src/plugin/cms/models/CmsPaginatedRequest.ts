export default interface CmsPaginatedRequest {
    domain: string,
    type: string,
    size: number,
    page: number,
    sortBy: string,
    sortOrder: string
}
