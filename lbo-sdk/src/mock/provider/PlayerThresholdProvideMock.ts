import PlayerThresholdProvider, {ExportConfig, ExportProgress, GenerateResponse } from '@/core/interface/provider/PlayerThresholdProvider';
import {ExportStatus} from "@/core/interface/provider/CsvGeneratorProvider";

export default class PlayerThresholdProviderMock implements PlayerThresholdProvider {

    currentProgress: ExportProgress = {
        status: ExportStatus.IDLE,
        comment: ''
    };

    generate(config: ExportConfig): Promise<GenerateResponse> {
        return new Promise((resolve,reject) => {
            return resolve({
                reference: 0,
                status: ExportStatus.COMPLETE,
                comment: ''
            })
        })
    }

    download(file: number): Promise<any> {

        return new Promise((resolve, reject) => {
            setTimeout(() => {

                let generatedFile = new Blob(["rivalani, hlengani, 2021-05-01 08:00, webkit"], { type: 'application/csv' }) as any;
                generatedFile["lastModifiedDate"] = "";
                generatedFile["name"] = file;

                var a = document.createElement("a"),
                    url = URL.createObjectURL(generatedFile);
                a.href = url;
                a.download = file.toString();
                document.body.appendChild(a);
                a.click();

                setTimeout(function () {
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                }, 0);

                resolve(generatedFile)
            }, 3000);
        });
    }

    progress(config: ExportConfig): Promise<ExportProgress> {
        return new Promise((resolve, reject) => {
            resolve(this.currentProgress);
        });
    }
    getConfig(): ExportConfig {
        return {
            domain: 'livescore_wa',
            reference: 0,
            provider: 'login-history',
            role: 'PLAYER_EXPORT_LOGIN_HISTORY',
            params:{}
        };
    }

    cancelGeneration(reference: number): Promise<void> {
        return new Promise((resolve, reject) => {});
    }

}
