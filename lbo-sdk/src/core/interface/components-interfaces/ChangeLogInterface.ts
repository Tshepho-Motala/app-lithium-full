import {DomainDropDown} from "@/core/interface/AutoWithdrawalInterface";

export  interface  ChangeLogItemInterface {
    authorFullName:string
    id:number
    author:string
    authorGuid:string
    changes: ChangeLogChangesItemInterface[]
    type:string
    entity:string
    [key: string]: any
}


export  interface  ChangeLogChangesItemInterface {
    editedBy: string
    field:string
    fromValue?:string
    id:number
    toValue:string
    [key: string]: any
}
export  interface  AuthorsListItemInterface {
    firstName: string
    email:string
    lastName:string
    username:string
    guid:string
    domain: DomainDropDown
    authorGuid?:string
    id:number
    toValue:string
    [key: string]: any
}
