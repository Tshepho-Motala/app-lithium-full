export class DataTableResponse<T> {
  public data: Array<T>;
  public draw: string;
  public recordsTotal: number;
  public recordsFiltered: number;
}
