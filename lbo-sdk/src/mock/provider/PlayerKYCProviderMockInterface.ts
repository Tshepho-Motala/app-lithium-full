
import PlayerKYCProviderInterface from "@/core/interface/provider/PlayerKYCProviderInterface";

export default class PlayerKYCProviderMockInterface  implements PlayerKYCProviderInterface {
    vendorData:any[] = []
    closeModal():Promise<any> {
        return new Promise((res, rej) => {});
    }
}