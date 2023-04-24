import { HttpClient } from '@angular/common/http';
import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';
import { TokenStorage } from 'app/services/token-storage.service';


@Injectable({
  providedIn: 'root'
})
export class TreeProofService {
  private readonly PRO_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";


  private readonly STANDARD1_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/criteria-proof";

  private readonly API_URL_FILE = this.httpUtilService.BASE_URL_EDITOR + this.httpUtilService.api.base + "/criteria-proof/proof/wopi/files/";


  constructor( 
     private httpUtilService: HttpUtilService, 
    private http: HttpClient,
    private tokenStorage: TokenStorage
    )
     { }
     public async getListPrograms(params: any){
      return await this.httpUtilService.callAPI(this.PRO_URL + "/selectbox", params).toPromise();
    }
    
  public async getTreeCriteria(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/setupTreeStardendCriteriaProofByProgramId/" +id, params).toPromise();
  }

  public async getTree(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/setupTreeStardendCriteriaProofByProgramId/" + id, params).toPromise();
  }

  public getURLEditor(id: number){
    return this.httpUtilService.EDITOR_URL + "WOPISrc=" + this.API_URL_FILE + id + "?access_token=" + this.tokenStorage.getTokenStr();
  }
}
