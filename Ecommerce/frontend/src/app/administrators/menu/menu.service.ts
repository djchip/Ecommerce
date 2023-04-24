import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/menu";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async addMenu(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }



  public async getListMenu(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-menu", params).toPromise();
  }

  public async getListMenuParrent(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-menu-parrent", params).toPromise();
  }

  public async getListMenuParrentUrl(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-menu-parrent-url", params).toPromise();
  }

  public async searchMenu(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async deleteMenu(params: any, menuId: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + menuId, params).toPromise();
  }

  public async detailMenu(params: any, menuId: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + menuId, params).toPromise();
  }

  public async editMenu(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

}