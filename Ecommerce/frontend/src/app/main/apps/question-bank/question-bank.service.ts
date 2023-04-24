import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
    providedIn: 'root'
})
export class QuestionBankService {
    private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + '/Question-Bank-Intent';

    constructor(
        private httpUtilService: HttpUtilService,
        private http: HttpClient
    ) { }



    public async getQuestionSimlilar(params: any) {
        return await this.httpUtilService.callAPI(this.httpUtilService.AI_URL + `gen_simlilar_words`, params).toPromise();
    }

    public async getIntentList(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + `/searchBotIntentDTOList`, params).toPromise();
    }

    public async getListIntentName(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + `/getListIntentName`, params).toPromise();
    }

    public async getEntitytList(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + `/getListEntityName`, params).toPromise();
    }

    public async getUserList(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + `/getListCreatedBy`, params).toPromise();
    }

    public async getSynonymList(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + `/getListSynonymName`, params).toPromise();
    }

    public async addIntent(params:any){
        return await this.httpUtilService.callAPI(this.API_URL,params).toPromise();
    }

}
