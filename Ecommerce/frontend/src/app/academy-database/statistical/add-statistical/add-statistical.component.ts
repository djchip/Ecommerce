import { FormService } from './../../form-management/form.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { TranslateService } from '@ngx-translate/core';
import { Component, OnInit, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { StatisticalService } from '../statistical.service';
import Swal from 'sweetalert2';
import { log } from 'console';

@Component({
  selector: 'app-add-statistical',
  templateUrl: './add-statistical.component.html',
  styleUrls: ['./add-statistical.component.scss']
})
export class AddStatisticalComponent implements OnInit {

  @Output() afterSaveReport = new EventEmitter<string>();
  @ViewChild('fileUpload') myFile : ElementRef;

  public isCollapsed;
  public addRPForm: FormGroup;
  public addRPFormSubmitted = false;
  public data;
  public isLoad = false;
  public isCheck = false;
  public isDisabled = false;
  public rowStart = "";
  public rowHeader = "";
  public fileTemp;
  public fileUpload: File;
  public listForm = [];
  public formKey = null;
  public listId = [];
  public id = window.localStorage.getItem("ISR")

  public listCal = [{id: 1, name: 'Tính tổng', nameEn: 'Sum', value: 'SUM'}, {id: 2, name: 'Đếm số lượng', nameEn: 'Count', value: 'COUNT'}, 
                    {id: 3, name: 'Giá trị lớn nhất', nameEn: 'Max', value: 'MAX'}, {id: 4, name: 'Giá trị nhỏ nhất', nameEn: 'Min', value: 'MIN'}];
  public columnCal = [{id: 1, name: 'Tính tổng', nameEn: 'Sum', value: 'SUM'}, {id: 2, name: 'Đếm số lượng', nameEn: 'Count', value: 'COUNT'}, 
                      {id: 3, name: 'Tính trung bình', nameEn: 'Average', value: 'AVG'},
                      {id: 4, name: 'Giá trị lớn nhất', nameEn: 'Max', value: 'MAX'}, {id: 5, name: 'Giá trị nhỏ nhất', nameEn: 'Min', value: 'MIN'}];
  public columnCalFull = [{id: 1, name: 'Tính tổng', nameEn: 'Sum', value: 'SUM'}, {id: 2, name: 'Đếm số lượng', nameEn: 'Count', value: 'COUNT'}, 
                      {id: 3, name: 'Tính trung bình', nameEn: 'Average', value: 'AVG'},
                      {id: 4, name: 'Giá trị lớn nhất', nameEn: 'Max', value: 'MAX'}, {id: 5, name: 'Giá trị nhỏ nhất', nameEn: 'Min', value: 'MIN'}, 
                      {id: 6, name: 'Cột trước - cột sau', nameEn: 'Sub', value: 'SUB'}, {id: 7, name: 'Tính tỉ lệ', nameEn: 'Ratio', value: 'RAT'}];
                      

  public items = [{ itemId: '', itemName: '', itemNameEn: '',itemQuantity: null, itemCost: null, itemUnit: false, itemSynthetic: false, itemRadio:'CSDL', isCollapsed: false, 
                  itemDb: [], itemLabel: [],
                  itemFieldDb: [], 
                  itemFieldRC: [], itemCalculate: null }];

  public item = {
    itemName: '',
    itemNameEn: '',
    itemQuantity: null,
    itemCost: null,
    itemUnit: true,
    itemSynthetic: true,
    itemRadio: 'CSDL',
    isCollapsed: true,
    itemDb: [],
    itemFieldDb: [],
    itemFieldRC: [],
    itemCalculate: null,
  };

  public currentLang = this._translateService.currentLang;

  constructor(private _translateService: TranslateService, private _changeLanguageService: ChangeLanguageService, private formBuilder: FormBuilder, 
              private service: StatisticalService, private formService: FormService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      });
   }

  addItem() {
    this.items.push({
      itemId: '',
      itemName: '',
      itemNameEn: '',
      itemQuantity: null,
      itemCost: null,
      itemUnit: false,
      itemSynthetic: false,
      itemRadio: 'CSDL',
      isCollapsed: false,
      itemDb: [],
      itemLabel: [],
      itemFieldDb: [],
      itemFieldRC: [],
      itemCalculate: null,
    });
  }

  addItemDbName(i) {
    // console.log(this.items[i].itemDb[0]);
    this.items[i].itemDb.push({
      itemDbId: '',
      itemDbName: null,
      itemDbYear: '',
    })
    // console.log(this.items[i].itemDb[0]);
    this.onChangeForm(i);
  }

  addItemFieldDb(i){
    this.items[i].itemFieldDb.push({
      itemFieldId: '',
      itemFieldName: null,
      itemFieldCalculate: null,
    })
  }

  addItemFieldRC(i){
    this.items[i].itemFieldRC.push({
      itemFieldId: '',
      itemFieldNameRC: null,
    })
  }

  deleteItem(id) {
    for (let i = 0; i < this.items.length; i++) {
      if (this.items.indexOf(this.items[i]) === id) {
        this.items.splice(i, 1);
        break;
      }
    }
  }

  deleteItemDb(id, db){
    if(this.items[id].itemDb.length === 1){
      this.items[id].itemFieldDb = [];
      this.items[id].itemCalculate = null;
    }
    for(let i=0; i < this.items[id].itemDb.length; i++){
      if(this.items[id].itemDb.indexOf(this.items[id].itemDb[i]) === db){
        this.items[id].itemDb.splice(i, 1);
        break;
      }
    }
    this.onChangeForm(id);
  }

  deleteItemFieldDb(id, db){
    if(this.items[id].itemFieldDb.length <= 2){
      this.items[id].itemCalculate = null;
    }
    for(let i=0; i < this.items[id].itemFieldDb.length; i++){
      if(this.items[id].itemFieldDb.indexOf(this.items[id].itemFieldDb[i]) === db){
        this.items[id].itemFieldDb.splice(i, 1);
        break;
      }
    }
  }

  deleteItemFieldRC(id, db){
    if(this.items[id].itemFieldRC.length <= 2){
      this.items[id].itemCalculate = null;
    }
    for(let i=0; i < this.items[id].itemFieldRC.length; i++){
      if(this.items[id].itemFieldRC.indexOf(this.items[id].itemFieldRC[i]) === db){
        this.items[id].itemFieldRC.splice(i, 1);
        break;
      }
    }
  }

  ngOnInit(): void {
    // this.initForm();
    this.items = [];
    if(this.id === null){
      this.initForm();
    } else {
      this.initForm();
      this.getReport(this.id);
    }
    this.searchForm();
  }

  onChange(i){
    if(this.items[i].itemRadio === 'CSDL'){
      this.items[i].itemCalculate = null;
      this.items[i].itemDb = [];
      this.items[i].itemFieldDb = [];
    } 
    if(this.items[i].itemRadio === 'BC') {
      this.items[i].itemCalculate = null;
      this.items[i].itemFieldRC = [];
    }
  }

  initForm(){
    this.addRPForm = this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required]],
      nameEn:[''],
      isNo: [false],
      items: [''],
      type: [''],
    })
  }

  fillForm(){
    this.addRPForm.patchValue({
      id: this.data.id,
      name: this.data.name,
      nameEn: this.data.nameEn,
      isNo: this.data.isNo,
      // items: this.data.items
    })
    // this.items = this.data.items;
  }

  get AddRPForm(){
    return this.addRPForm.controls;
  }

  addStatisticalReport(){
    this.addRPFormSubmitted = true;
    if(this.addRPForm.value.name !== ''){
      this.addRPForm.patchValue({
        name: this.addRPForm.value.name.trim()
      })
    }
    this.isCheck = false;
    if(this.addRPForm.invalid || this.items.length === 0){
      return;
    }

    this.items.forEach((item) => {
      if(item.itemName.trim() === ''){
        this.isCheck = true;
        item.itemName = '';
      }
    })

    if(this.isCheck) return;

    this.addRPForm.setValue['items'] = this.items;
    let content = this.addRPForm.value;
    content.items = JSON.stringify(this.items);
    if(this.formKey != null){
      content.type = "CSDL";
    }
    // console.log(content);
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service.statisticalReport(params)
    .then((data) => {
      let response = data;
      if (response.code === 0) {
        let title;
        if(this.id === null){
          title = this._translateService.instant('MESSAGE.REPORT.ADD_SUCCESS');
        } else {
          title = this._translateService.instant('MESSAGE.REPORT.UPDATE_SUCCESS');
        }
        Swal.fire({
          icon: "success",
          title: title,
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
          this.afterSaveReport.emit('completed');
        });
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
    })
  }

  getReport(id){
    let params = {
      method: "GET"
    };
    Swal.showLoading();
      this.service
      .doRetrieve(id, params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.data = response.content;
          this.fillForm();
          this.items = JSON.parse(response.content.items);
          // this.items = response.content.items;
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

  onChangeForm(i){
    let id = "";
    this.listId = [];
    this.items[i].itemLabel = [];
    this.items[i].itemFieldDb = [];
    this.items[i].itemDb.forEach((db) =>{
      if(db.itemDbName != null){
        this.listId.push(db.itemDbName);
        id = id + db.itemDbName + ",";
      }
    })
    
    // let content = {listId: this.listId};

    // let params = {
    //   method: "GET",
    //   content: content,
    // };
    let params = {
      method: "GET", listId: id
    };
    this.service.getLabel(params)
    .then((data) => {
      let response = data;
      if (response.code === 0) {
        for(let j = 0; j< data.content.length ; j++){
          this.items[i].itemLabel.push(data.content[j]);
        }
        // console.log(this.items[i].itemLabel);
      } else {
        
      }
    })
    .catch((error) => {
      
    })
  }

  searchForm() {
    let params = {
      method: "GET",
      name: "",
      yearOfApplication: "",
      uploadTime: "",
      currentPage: 0,
      perPage: 10000,
    };
    this.formService
      .searchForm(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          for(let i = 0; i< data.content["items"].length ; i++){
            let a = data.content["items"][i]["yearOfApplication"] + " - " + data.content["items"][i]["name"];
            data.content["items"][i]["name"] = a
          }
          this.listForm = response.content["items"];
          // console.log("Form: ", this.listForm);
        } else {

          if (response.code === 2) {
            
          }
        }
      })
      .catch((error) => {
        
      });
  }

  onChangeDb(){
    // console.log(this.formKey);
    if(this.formKey == null){
      this.items = [];
    } else {
      this.myFile.nativeElement.value = "";
      this.fileTemp = "";
      this.items = [];
      this.isLoad = false;
      this.rowStart = "";
      this.rowHeader = "";
      this.isDisabled = false;
      let params = {
        method: "GET"
      };
      // Swal.showLoading();
        this.service
        .getLabelByKeyForm(params, this.formKey.formKey)
        .then((data) => {
          // Swal.close();
          let response = data;
          if (response.code === 0) {
            this.items = [];
            for(let i=0; i<data.content.length; i++){
              this.items.push({
                itemId: '',
                itemName: data.content[i]["value"],
                itemNameEn: '',
                itemQuantity: null,
                itemCost: null,
                itemUnit: false,
                itemSynthetic: false,
                itemRadio: 'CSDL',
                isCollapsed: true,
                itemDb: [{itemDbId: '', itemDbName: this.formKey.id, itemDbYear: '',}],
                itemLabel: data.content,
                itemFieldDb: [{itemFieldId: '', itemFieldName: data.content[i]["id"], itemFieldCalculate: null,}],
                itemFieldRC: [],
                itemCalculate: null,
              });
            }
          } else {
            Swal.fire({
              icon: "warning",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.close();
          Swal.fire({
            icon: "warning",
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
      
    }
  }

  onChangeFile(event){
    if(event.target.files.length > 0){
      this.isDisabled = false;
      this.formKey = null;
      this.fileTemp = event.target.files[0].name;
      this.fileUpload = event.target.files[0];
    }
  }

  onReSet(){
    this.myFile.nativeElement.value = "";
    this.fileTemp = "";
    this.formKey = null;
    this.items = [];
    this.isLoad = false;
    this.rowStart = "";
    this.rowHeader = "";
    this.isDisabled = false;
    this.isCheck = false;
  }

  onChangeRow(){
    this.isDisabled = false;
  }

  onLoad(){
    if(this.fileTemp === "" || this.rowStart === "" || this.rowHeader === ""){
      return;
    } else {
      this.isLoad = true;
    }
    this.isDisabled = true;

    let params = {
      method: "POST",
      content: {rowStart: this.rowStart, rowHeader: this.rowHeader}
    };
    this.service.doImportTemplate(params, this.fileUpload)
      .then((data) => {
        this.isLoad = false;
        let response = data;
        if(response.code === 0){
          this.items = [];
            for(let i=0; i<data.content.length; i++){
              this.items.push({
                itemId: '',
                itemName: data.content[i]["value"],
                itemNameEn: '',
                itemQuantity: null,
                itemCost: null,
                itemUnit: false,
                itemSynthetic: false,
                itemRadio: 'CSDL',
                isCollapsed: true,
                itemDb: [],
                itemLabel: [],
                itemFieldDb: [],
                itemFieldRC: [],
                itemCalculate: null,
              });
            }
        } else if(response.code === 7){
          Swal.fire({
            icon: "warning",
            title: this._translateService.instant('MESSAGE.REPORT.WRONG_FORMAT'),
          })
          this.onReSet();
        } else if(response.code === 2){
          this.onReSet();
          Swal.fire({
            icon: "warning",
            title: this._translateService.instant('MESSAGE.REPORT.DATA_EXIST'),
          })
        } else {
          Swal.fire({
            icon: "warning",
          })
        }
      }).catch((error) => {
        this.isLoad = false;
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      })

  }

}
