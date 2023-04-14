import { Brand } from './brand';

export class Campaign {

  public guid: string;
  public name: string;
  public archived: boolean;
  public deleted: boolean;
  public published: boolean;
  public createdDate: number;
  public brand: Brand;

  constructor(public id: number) {
  }
}
