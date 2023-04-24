import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/product-category";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async searchCategory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-all", params).toPromise();
  }

  public async getListCategory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-all", params).toPromise();
  }

  public async addCategory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add-category", params).toPromise();
  }

  public async editCategory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/update", params).toPromise();
  }

  public async detailCategory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/detail-category/" + id, params).toPromise();
  }

  public async deleteCategory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + id, params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
