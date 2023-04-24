import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/product";

  constructor(
    private httpUtilService: HttpUtilService,
    private http: HttpClient
  ) { }

  public async getListCategory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-list-category", params).toPromise();
  }

  public async getListProduct(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-list-product", params).toPromise();
  }

  public async searchProduct(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-all", params).toPromise();
  }

  public async addProduct(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add-product", params).toPromise();
  }

  public async detailProduct(params: any, id: number){
    return await this.httpUtilService.callAPI(this.API_URL + "/detail-product/" + id, params).toPromise();
  }

  public async addProductImg(params: any, files: FileList){
    return await this.httpUtilService.callAPIUploadImg(this.API_URL + "/add-product-image", files, params).toPromise();
  }

  public async editProduct(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/edit-product", params).toPromise();
  }

  public async deleteProduct(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + id, params).toPromise();
  }
}
