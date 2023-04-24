import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DatabaseService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database";
  private readonly API_UNIT = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit/get-unit";
  private readonly API_DB_OBJECT = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database-object";
  private readonly API_STUDENT = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/student";
  private readonly API_STAFF = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/staff";
  private readonly API_EDU = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/program-edu";
  private readonly API_REWARD = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/emulation-reward";
  private readonly API_TEAM = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/team-edu";
  private readonly API_LIST_JOIN = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/list-join-edu";
  private readonly API_TOPIC = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/topic";
  private readonly API_LIST_TOPIC = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database/list-topic";
  private readonly API_OBJECT_DETAIL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/object-detail";
  private readonly API_DB_OBJ = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database-obj";
  private readonly API_IMPORT = this.httpUtilService.api.base + "/database-obj/import-excel";


  constructor(private httpUtilService: HttpUtilService) { }

  importDb(params: any, file: File): Promise<any>{
    return this.httpUtilService.uploadMultipartFileWithFormData(this.API_IMPORT, file, params).toPromise();
  }

  public async download(id: number, fileName: string){
    return await this.httpUtilService.onDownloadFile(this.API_DB_OBJ + "/export-temp/" + id, fileName);
  }

  public async dbObj(params: any){
    return await this.httpUtilService.callAPI(this.API_DB_OBJ + "/page", params).toPromise();
  }

  public async getDbObj(params: any, id: number){
    return await this.httpUtilService.callAPI(this.API_DB_OBJ + "/" + id, params).toPromise();
  }

  //Object Detail
  public async objDetail(params: any){
    return await this.httpUtilService.callAPI(this.API_OBJECT_DETAIL, params).toPromise();
  }

  public async getObjDetail(params: any, id: number){
    return await this.httpUtilService.callAPI(this.API_OBJECT_DETAIL + "/" + id, params).toPromise();
  }

  // Cơ sở dữ liệu chung

  public async searchDbCommon(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async getUnit(params: any){
    return await this.httpUtilService.callAPI(this.API_UNIT, params).toPromise();
  }

  public async getDbCommonByCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/code", params).toPromise();
  }

  public async getDbCommonById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async deleteDbCommon(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async doSaveDbCommon(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  // Đối tượng cơ sở dữ liệu
  public async searchDbObject(params: any){
    return await this.httpUtilService.callAPI(this.API_DB_OBJECT, params).toPromise();
  }

  public async getDbObjectById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_DB_OBJECT + "/" + id, params).toPromise();
  }

  public async deleteDbObject(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_DB_OBJECT + "/" + id, params).toPromise();
  }

  public async doSaveDbObject(params: any){
    return await this.httpUtilService.callAPI(this.API_DB_OBJECT, params).toPromise();
  }
  // Student
  public async callAPIStudent(params: any){
    return await this.httpUtilService.callAPI(this.API_STUDENT, params).toPromise();
  }

  public async callAPIStudentById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_STUDENT + "/" + id, params).toPromise();
  }
  // STAFF
  public async callAPIStaff(params: any){
    return await this.httpUtilService.callAPI(this.API_STAFF, params).toPromise();
  }

  public async callAPIStaffById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_STAFF + "/" + id, params).toPromise();
  }
  // PROGRAM EDU
  public async callAPIEdu(params: any){
    return await this.httpUtilService.callAPI(this.API_EDU, params).toPromise();
  }

  public async callAPIEduById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_EDU + "/" + id, params).toPromise();
  }
  // REWARD
  public async callAPIReward(params: any){
    return await this.httpUtilService.callAPI(this.API_REWARD, params).toPromise();
  }

  public async callAPIRewardById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_REWARD + "/" + id, params).toPromise();
  }
  // TEAM 
  public async callAPITeam(params: any){
    return await this.httpUtilService.callAPI(this.API_TEAM, params).toPromise();
  }

  public async callAPITeamById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_TEAM + "/" + id, params).toPromise();
  }
  // LIST JOIN EDU
  public async callAPIListJoin(params: any){
    return await this.httpUtilService.callAPI(this.API_LIST_JOIN, params).toPromise();
  }

  public async callAPIListJoinById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_LIST_JOIN + "/" + id, params).toPromise();
  }
  // TOPIC
  public async callAPITopic(params: any){
    return await this.httpUtilService.callAPI(this.API_TOPIC, params).toPromise();
  }

  public async callAPITopicById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_TOPIC + "/" + id, params).toPromise();
  }
  // LIST TOPIC
  public async callAPIListTopic(params: any){
    return await this.httpUtilService.callAPI(this.API_LIST_TOPIC, params).toPromise();
  }

  public async callAPIListTopicById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_LIST_TOPIC + "/" + id, params).toPromise();
  }
}
