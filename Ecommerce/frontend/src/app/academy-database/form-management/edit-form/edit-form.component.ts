import { id } from '@swimlane/ngx-datatable';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { formatDate } from '@angular/common';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FormService } from '../form.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from './../../../services/change-language.service';
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import { StatisticalService } from 'app/academy-database/statistical/statistical.service';
@Component({
  selector: 'app-edit-form',
  templateUrl: './edit-form.html',
  styleUrls: ['./edit-form.scss']
})
export class EditFormComponent implements OnInit {

  @Output() afterEditMenu = new EventEmitter<string>();

  public rows;
  public contentHeader: object;
  public editFG: FormGroup;
  public addUserFormSubmitted = false;
  public data;
  public form_id;
  public listObject = [];
  public listUnit = [];
  public currentLang = this._translateService.currentLang;
  public currentUserName;
  public changeFile = false;
  public items = [];
  public addRPForm: FormGroup;
  fileName: String;
  constructor(private _changeLanguageService: ChangeLanguageService,
    private formBuilder: FormBuilder, private service: FormService, private statisticalService: StatisticalService,
    public _translateService: TranslateService, private statisticalReport: StatisticalService, 
    private _coreTranslationService: CoreTranslationService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.form_id = window.localStorage.getItem("form_id");
    this.getListUnit();
    this.initForm();
    this.getFormDetail();
  }

  initForm() {
    this.editFG = this.formBuilder.group(
      {
        id: [null, [Validators.required]],
        name: [null, [Validators.required]],
        nameEn: [null],
        yearOfApplication: [null, [Validators.required]],
        fileName: [null, [Validators.required]],
        uploadTime: [null, [Validators.required]],
        units: [null, [Validators.required]],
        numTitle: [null],
        startTitle: [null],
        pathFile: ''
      },
    );
  }
  onDownload(fileName) {
    Swal.showLoading();
    // var splitted = fileName.split("\\");
    var splitted = this.data.pathFile.split("/");
    const fileName_ = splitted[splitted.length - 1];

    console.log(fileName_ + '  FILE NAME');

    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/form-file/' + fileName_);
    link.setAttribute('download', fileName_);
    document.body.appendChild(link);
    link.click();
    link.remove();
    Swal.close();
  }

  onDownloadFile() {
    this.service.download(this.data.id, this.data.fileName);
  }

  downloadForms() {
    this.listRecord.map(e => {
      this.onDownload(e.pathFile);
    })
  }
  listRecord = []
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
      for (let i = 0; i < listLength; i++) {
        if (this.listRecord[i].id == elementRemove.id) {
          this.listRecord.splice(i, 1);
          break;
        }
      }
    };
  }
  fillForm() {
    var date = new Date(this.data.uploadTime != null ? this.data.uploadTime : null);
    const format = 'yyyy-MM-dd';
    const locale = 'en-US';
    const formattedDate = formatDate(date, format, locale) != null ? formatDate(date, format, locale) : null;
    this.editFG.patchValue({
      id: this.data.id,
      name: this.data.name,
      nameEn: this.data.nameEn,
      yearOfApplication: this.data.yearOfApplication,
      units: this.data.units,
      uploadTime: formattedDate,
      fileName: this.data.fileName,
    });
  }

  getListUnit() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getUnit(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.close();
          this.listUnit = data.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  async getFormDetail() {
    if (this.form_id) {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailForm(params, this.form_id)
        .then((data) => {
          let response = data;
          if (response.code == 0) {
            Swal.close();
            this.data = response.content;
            this.fillForm();
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
    }
  }

  resetForm() {
    this.fillForm();
  }

  attachments
  onFileChange(event) {
    this.changeFile = true;
    if (event.target.files.length > 0) {
      this.attachments = event.target.files[0];

      // console.log('atm ' + event.target.files);
      this.fileName = this.attachments.name;
      this.editFG.patchValue({
        fileName: this.attachments.name
      })

      console.log('atm ' + this.attachments.name);

      console.log('atm2 ' + typeof (this.attachments.name));
    }
  }

  get EditForm() {
    return this.editFG.controls;
  }

  dataSource
  uploadForm() {
    this.addUserFormSubmitted = true;
    if (this.editFG.value.name !== '') {
      this.editFG.patchValue({
        name: this.editFG.value.name.trim()
      })
    }
    if (this.editFG.invalid) {
      return;
    }

    if (this.changeFile) {
      if (this.editFG.value.numTitle == null) {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.FORM.NUM_TITLE'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
        });
        return;
      } else {
        var re = new RegExp("^[1-9][0-9]*$");
        if (!re.test(this.editFG.value.numTitle)) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.FORM.ONLY_POSITIVE_INTEGER'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
          return;
        }
      }

      if (this.editFG.value.startTitle == null) {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.FORM.ROW_START_RQ'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
        });
        return;
      } else {
        var re = new RegExp("^[1-9][0-9]*$");
        if (!re.test(this.editFG.value.startTitle)) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.FORM.ONLY_POSITIVE_INTEGER'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
          return;
        }
      }
    }
    let inputDate = new Date(this.editFG.value.uploadTime);
    let date = new Date();
    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    if (inputDate.getTime() < date.getTime()) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.FORM.INVALID_YEAR'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      }).then((result) => {
      });
      return;
    }
    if (typeof (this.fileName) == 'undefined') {
      console.log(" file name " + typeof (this.fileName));
      let content = this.editFG.value;
      let params = {
        method: "PUT",
        content: content,
      };
      Swal.showLoading();
      this.service.editFormWithoutFile(params).then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.FORM.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditMenu.emit('completed');
          });

          //quannv
          let params = {
            method: "GET"
          };
          this.statisticalService.getLabelByKeyForm(params, this.data.formKey)
          .then((label) => {
            let response = label;
          if (response.code === 0) {
            this.items = [];
            for(let i=0; i<label.content.length; i++){
              this.items.push({
                itemId: '',
                itemName: label.content[i]["value"],
                itemNameEn: '',
                itemQuantity: null,
                itemCost: null,
                itemUnit: false,
                itemSynthetic: false,
                itemRadio: 'CSDL',
                isCollapsed: true,
                itemDb: [{itemDbId: '', itemDbName: this.data.id, itemDbYear: '',}],
                itemLabel: label.content,
                itemFieldDb: [{itemFieldId: '', itemFieldName: label.content[i]["id"], itemFieldCalculate: null,}],
                itemFieldRC: [],
                itemCalculate: null,
              });
            }
  
            let params = {
              method: "GET"
            };
            this.statisticalReport.doRetrieveByForm(this.data.id, params)
              .then((report) => {
                let response = report;
                if(response.code === 0){
                  this.addRPForm = this.formBuilder.group({
                    id: [report.content['id']],
                    name: [this.editFG.value.name],
                    nameEn:[this.editFG.value.nameEn],
                    isNo: [false],
                    items: [JSON.stringify(this.items)],
                    formId: [this.data.id]
                  })
        
                  let params = {
                    method: "POST",
                    content: this.addRPForm.value,
                  };
                  this.statisticalService.statisticalReport(params);
                }
              })
          }
          })
          //end

        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.FORM.FORM_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          })
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

    } else {
      console.log("numTitle " + this.editFG.value.numTitle);

      Swal.showLoading();
      this.service.uploadForm({
        content: {
          formName: this.editFG.value.name,
          year: this.editFG.value.yearOfApplication,
          numTitle: parseInt(this.editFG.value.numTitle),
          // year: this.editFG.value.yearOfApplication ? this.editFG.value.yearOfApplication + 0 : 0,
          // numTitle: this.data.numTitle,
          isForm: true,
          formKey: this.data.formKey,
          startTitle: parseInt(this.editFG.value.startTitle),
        }
      }, this.attachments).then((data) => {
        Swal.close();
        if (data.body.code === 0) {
          this.dataSource = data.body.content;
          this.editForm();
        }
      });
    }
  }

  editForm() {
    let content = this.editFG.value;
    content.fileName = this.attachments.name;
    content.pathFile = this.dataSource.pathFile;
    content.numTitle = this.dataSource.numTitle;
    content.formKey = this.dataSource.formKey + 0;
    content.startTitle = this.dataSource.startTitle;
    let params = {
      method: "PUT",
      content: content,
    };
    Swal.showLoading();
    this.service.editForm(params).then((data) => {
      Swal.close();
      let response = data;
      if (response.code === 0) {
        Swal.fire({
          icon: "success",
          title: this._translateService.instant('MESSAGE.FORM.UPDATE_SUCCESS'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        }).then((result) => {
          this.afterEditMenu.emit('completed');
        });

        // quannv
        
        //end quannv

      } else if (response.code === 3) {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.FORM.FORM_EXISTED'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        })
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
