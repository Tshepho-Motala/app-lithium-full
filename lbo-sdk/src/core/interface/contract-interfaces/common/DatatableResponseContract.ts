interface DataTableResponse<T> {
    data: Array<T>;
    recordsTotal: number;
    recordsFilterd: number;
    draw: string;
}