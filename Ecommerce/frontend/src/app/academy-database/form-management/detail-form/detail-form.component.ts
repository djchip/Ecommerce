import { Component, Input, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { FormService } from '../form.service';
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from './../../../services/change-language.service';
import * as _ from 'lodash';
@Component({
  selector: 'app-detail-form',
  templateUrl: './detail-form.html',
  styleUrls: ['./detail-form.scss']
})
export class DetailFormComponent implements OnInit {

  @Input() id;
  @Input() obj;
  @Input() objRevert;
  public dataRevert = {
    id:null,
    code:null,
    name: null,
    nameEn: null,
    // form_des: null,
    // formDesEn:null,
    createBy: null,
    createDate: null,
    fileName:null,
    units:null,
    yearOfApplication:null,
    objects:null,
    updateBy: null,
    updateDate: null,
    
   };
   public dateFormat = window.localStorage.getItem("dateFormat");

  public contentHeader: object;
  // public FormId;
  public data: any;
  public currentLang = this._translateService.currentLang;
  public rows;
  fileName: String;
  constructor(private _changeLanguageService: ChangeLanguageService,private router: Router, private service: FormService,  public _translateService: TranslateService) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {

     if(this.id != null){
      this.getFormDetail();

     } else{
       this.data=this.obj;
       if(this.objRevert !== null){
        this.compareObj();
      }
     }

     console.log("data +" , this.data)
  }

  getFormDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailForm(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.close();
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),

          });
        });
    }
  }
  onDownload(fileName) {
    Swal.showLoading();
    // var splitted = fileName.split("\\");
    var splitted = this.data.fileName.split("/");
    const fileName_ = splitted[splitted.length - 1];
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/form-file/' + fileName_);
    link.setAttribute('download', fileName_);
    document.body.appendChild(link);
    link.click();
    link.remove();
    Swal.close();
  }

  downloadForm(id, fileName) {
    console.log("ID " + id);
    console.log("NAME " + fileName);
    
    this.service.download(id, fileName);
  }
  downloadForms() {
    this.listRecord.map(e => {
      this.onDownload( e.pathFile);
    })
  }
  listRecord=[]
  onCheckboxChange(event, index) {
    const isChecked = event.target.checked;
    if (isChecked) {
      this.rows[index].isChecked = true;
      //thêm element đã chọn vào list cần xóa
      this.listRecord.push(this.rows[index]);
    }
    else {
      this.rows[index].isChecked = false;
      // Xóa element đã bỏ chọn khỏi list cần xóa
      let listLength = this.listRecord.length;
      let elementRemove = this.rows[index];
      for(let i=0;i<listLength;i++){
        if(this.listRecord[i].id == elementRemove.id){
          this.listRecord.splice(i,1);
          break;
        }
      }
    };
  }
  compareObj() {
    this.obj.id === this.objRevert.id ? this.dataRevert.id = null : this.dataRevert.id = this.objRevert.id;
    this.obj.code === this.objRevert.code ? this.dataRevert.code = null : this.dataRevert.code = this.objRevert.code;

    this.obj.name === this.objRevert.form_name ? this.dataRevert.name = null : this.dataRevert.name = this.objRevert.name;
    this.obj.nameEn === this.objRevert.nameEn ? this.dataRevert.nameEn = null : this.dataRevert.nameEn = this.objRevert.nameEn;

    // this.obj.form_des === this.objRevert.form_des ? this.dataRevert.form_des = null : this.dataRevert.form_des = this.objRevert.form_des;
    // this.obj.formDesEn === this.objRevert.formDesEn ? this.dataRevert.formDesEn = null : this.dataRevert.formDesEn = this.objRevert.formDesEn;
    this.obj.fileName === this.objRevert.fileName ? this.dataRevert.fileName = null : this.dataRevert.fileName = this.objRevert.fileName;

    _.isEqual(this.obj.units, this.objRevert.units) ? this.dataRevert.units = null : this.dataRevert.units = this.objRevert.units;

    this.obj.objects === this.objRevert.objects ? this.dataRevert.objects = null : this.dataRevert.objects = this.objRevert.objects;



    this.obj.createBy === this.objRevert.createBy ? this.dataRevert.createBy = null : this.dataRevert.createBy = this.objRevert.createBy;
    this.obj.createDate === this.objRevert.createDate ? this.dataRevert.createDate = null : this.dataRevert.createDate = this.objRevert.createDate;
    this.obj.updateBy === this.objRevert.updateBy ? this.dataRevert.updateBy = null : this.dataRevert.updateBy = this.objRevert.updateBy;
    this.obj.updateDate === this.objRevert.updateDate ? this.dataRevert.updateDate = null : this.dataRevert.updateDate = this.objRevert.updateDate;
  }
}
