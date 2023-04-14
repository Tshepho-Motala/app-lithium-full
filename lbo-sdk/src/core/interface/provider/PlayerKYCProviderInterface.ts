export default interface PlayerKYCProviderInterface {
    vendorData: any[]
    closeModal():Promise<any>
}