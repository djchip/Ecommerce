import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { TranslateModule } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class CategoriesManagementService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/categories";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async addCategories(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }



  public async getListCategories(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-catogories", params).toPromise();
  }


  public async searchCategories(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async deleteCategories(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete"+"/" + id, params).toPromise();
  }

  public async detailCategories(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-by-id"+"/" + id, params).toPromise();
  }

  public async editCategories(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
