import {AxiosInstance, Method, ResponseType} from "axios";
export  interface ApiClientInterface<T> {
   provider: T // (Add your provider type, can be different types add '| FetchInstance' if want use fetch)
   responseErrorFunction(error) :ApiConfigInterface | Error,
   reloadTokenAngular(): void
}

export interface ApiConfigInterface<D = any> {
   url?: string;
   method?: Method;
   baseURL?: string;
   headers?: any;
   params?: any;
   paramsSerializer?: (params: any) => string;
   data?: D;
   timeout?: number;
   timeoutErrorMessage?: string;
   withCredentials?: boolean;
   responseType?: ResponseType;
   xsrfCookieName?: string;
   xsrfHeaderName?: string;
   onUploadProgress?: (progressEvent: any) => void;
   onDownloadProgress?: (progressEvent: any) => void;
   maxContentLength?: number;
   validateStatus?: ((status: number) => boolean) | null;
   maxBodyLength?: number;
   maxRedirects?: number;
   socketPath?: string | null;
   httpAgent?: any;
   httpsAgent?: any;
   decompress?: boolean;
   insecureHTTPParser?: boolean;
   [key: string]: any
}

