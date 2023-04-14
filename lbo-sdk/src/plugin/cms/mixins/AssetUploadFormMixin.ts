import { RootScopeInterface } from "@/core/interface/ScopeInterface";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import { debounce } from "@/core/utils/helpers";
import { isNull } from "@/core/utils/objectUtils";
import { slug } from "@/core/utils/stringUtils";
import { Component, Inject, Prop, Vue, Watch } from "vue-property-decorator";
import CmsAssetType from "../models/CmsAssetType";




@Component
export default class AssetUploadFormMixin extends Vue {
    @Prop({
        type: Boolean,
        default: false,
    })
    visible: boolean = false;

    @Prop({
        type: String,
        required: true,
    })
    type!: CmsAssetType;

    @Prop({
        type: Object,
        required: true,
    })
    config!: any;

    @Prop({
        type: String,
        required: true,
    })
    domain!: string;

    

    valid: boolean = false;
    uploading: boolean = false;
    progress: number = 0;

    errorMessages = {
        name: [] as Array<string>,
        file: [] as Array<string>
      }

    required = (v: any) => !!v || 'Field is required';
    notEmpty = (v: any) => (v && v.size > 0) || 'Field is required';

    @Inject('rootScope') readonly rootScope!: RootScopeInterface;
    @Inject('logService') logService!: LogServiceInterface;

    @Watch('form.name')
    onNameChange = debounce(async (newVal: string, oldVal: string) => {
        if (newVal && newVal.length > 1) {
            let response = await this.rootScope.provide.cmsProvider.findAssetByNameAndDomainAndType(
                slug(newVal),
                this.domain,
                this.type
            );

            this.errorMessages.name = [];

            if (!isNull(response)) {
                this.errorMessages.name.push("This field must be unique");
            }
        }
    }, 200)

    onProgress(progress: number) {
        this.progress = Math.floor(progress);
    }

    uploadSuccess(message: string) {
        this.$emit('upload-complete', { 
            message: message, 
            type: 'success',
            assetType: this.type
        })
    }

    uploadError(message: string) {
        this.$emit('upload-error', { 
            message: message, 
            type: 'error',
            assetType: this.type
        })
    }
    
    cancel() {
        const form = this.$refs[this.type] as any;
        form.reset()
        this.$emit('upload-close')
    }
}