import {Injectable} from '@angular/core';
import {HttpUtilService} from "../../services/http-util.service";

@Injectable({
    providedIn: 'root'
})
export class EmailConfigService {
    private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/email-config";
    private readonly USER_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";

    constructor(
        private httpUtilService: HttpUtilService
    ) {
    }

    public async detailEmailConfig(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
    }

    public async updateEmailConfig(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
    }

    public async sendEmail(params: any, files: File) {
        return await this.httpUtilService.callAPIUploadFilesAndData(this.API_URL+"/send" ,files, params).toPromise();
    }

    public async getMailTo(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL+"/get-mail-to", params).toPromise();
    }

    public async searchUser(params: any){
        return await this.httpUtilService.callAPI(this.USER_URL, params).toPromise();
      }
}
