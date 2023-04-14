import { Brand } from './brand';

export class Ad {

  public brand: Brand;
  public name: string;
  public guid: string;
  public type: number;
  public entryPoint: string;
  public targetUrl: string;

  public resourceUrlSystem: string;
  public entryPointUrlSystem: string;
  public icon: string;
  public code: string;

  constructor(
    public id: number
    ){}
}
