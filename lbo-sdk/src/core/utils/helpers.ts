import {DataOptions, DataTableHeader} from "vuetify";

export const debounce = (callback: any, wait: number) => {
    let timeout:any = null
    return (...args: any) => {
      const next = () => callback(...args)

      clearTimeout(timeout)
      timeout = setTimeout(next, wait)
    }
  }

export function constructVDataTablesRequest(options: DataOptions, headers: DataTableHeader[]): { [x: string]: string | boolean | number } {
    const {
        page,
        itemsPerPage,
        sortBy,
        sortDesc,
        groupBy,
        groupDesc,
        mustSort,
        multiSort
    } = options

    let dtQueryParams: { [x: string]: string | boolean | number } = {}
    headers.forEach((header, index) => {
        dtQueryParams[encodeURI("columns[" + index + "][data]")] = header.value
        dtQueryParams[encodeURI("columns[" + index + "][name]")] = ''
        dtQueryParams[encodeURI("columns[" + index + "][searchable]")] = header.filterable !== undefined ? header.filterable : true
        dtQueryParams[encodeURI("columns[" + index + "][orderable]")] = header.sortable !== undefined ? header.sortable : true
        dtQueryParams[encodeURI("columns[" + index + "][search][value]")] = ''
        dtQueryParams[encodeURI("columns[" + index + "][search][regex]")] = false

        if (sortBy !== undefined && Array.isArray(sortBy) && sortBy.length > 0) {
            if (sortBy[0] === header.value) {
                dtQueryParams[encodeURI("order[0][column]")] = index
                dtQueryParams[encodeURI("order[0][dir]")] = (sortDesc[0] ? "desc" : "asc")
            }
        }
    })

    dtQueryParams[encodeURI("search[value]")] = ""
    dtQueryParams[encodeURI("search[regex]")] = false
    return dtQueryParams;
}