import { HttpUtilService } from 'app/services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LogUndoService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/undo-log";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async searchUndo(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async undo(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/undo", params).toPromise();
  }
}
