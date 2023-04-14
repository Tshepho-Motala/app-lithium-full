import QuickActionProviderInterface from '@/core/interface/provider/QuickActionProviderInterface'

export default class QuickActionProviderMock implements QuickActionProviderInterface {
    menuItems = [];
    user = null
}
