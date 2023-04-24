import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";
  constructor(private httpUtilService: HttpUtilService) { }

  public async forgotpassword(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/forgot-password" , params).toPromise();
  }

  // public async resettpassword(params: any){
  //   return await this.httpUtilService.callAPI(this.API_URL + "/reset_password" , params).toPromise();
  // }

}
