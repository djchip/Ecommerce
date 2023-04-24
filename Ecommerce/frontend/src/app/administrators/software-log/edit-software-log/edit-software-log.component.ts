import { ChangeDetectorRef,Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SoftwareLogService } from '../software-log.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-edit-software-log',
  templateUrl: './edit-software-log.component.html',
  styleUrls: ['./edit-software-log.component.scss']
})
export class EditSoftwareLogComponent implements OnInit {
  @Output() afterEditSoftwareLog = new EventEmitter<string>();

  public contentHeader: object;
  public addErrorLogForm: FormGroup;
  public addErrorLogSubmitted = false;
  public data;
  public id;
  public currentLang = this._translateService.currentLang;
  public listStatus = [{id:"0", nameEn: "Bug", name: "Lỗi"}, 
                      {id:"1", nameEn: "Fixed", name: "Đã sửa"}, 
                      {id:"2", nameEn: "Closed", name: "Đã đóng"},
                      {id:"3", nameEn: "Cancel", name: "Làm lại"}];
  constructor(private ref: ChangeDetectorRef, private modalService: NgbModal,private formBuilder: FormBuilder, private service: SoftwareLogService,  public _translateService: TranslateService, private _changeLanguageService: ChangeLanguageService) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    this.id = window.localStorage.getItem("id");
    this.getDirectoryDetail();
   }
  initForm(){
    this.addErrorLogForm = this.formBuilder.group(
      {
        id:['',[Validators.required]],
        error: ['',[Validators.required]],
        amendingcontent: ['',[Validators.required]],
        version: ['',[Validators.required]],
        note: ['',[Validators.required]],
        status: [null, [Validators.required]],
      },
    );
  }
  fillForm(){
    let statusObj;
    this.listStatus.forEach(element => {
      if(element.id == this.data.status){
        statusObj = element;
      }
    });
    this.addErrorLogForm.patchValue(
      {
        id: this.data.id,
        error: this.data.error,
        amendingcontent: this.data.amendingcontent,
        version: this.data.version,
        note: this.data.note,
        status:statusObj
        // status:this.data.status,
      },
    );
    console.log(JSON.stringify(this.addErrorLogForm.value))
  }
  get AddErrorLogForm(){
    return this.addErrorLogForm.controls;
  }
  async getDirectoryDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailSoftwareLog(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            console.log("DATA",this.data);
            this.fillForm();
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

  editError(){
    this.addErrorLogSubmitted = true;

    if(this.addErrorLogForm.value.error !== ''){
      this.addErrorLogForm.patchValue({
        error: this.addErrorLogForm.value.error.trim()
      })
    }
    if(this.addErrorLogForm.value.amendingcontent !== ''){
      this.addErrorLogForm.patchValue({
        amendingcontent: this.addErrorLogForm.value.amendingcontent.trim()
      })
    }
    if(this.addErrorLogForm.value.version !== ''){
      this.addErrorLogForm.patchValue({
        version: this.addErrorLogForm.value.version.trim()
      })
    }
    if(this.addErrorLogForm.value.note !== ''){
      this.addErrorLogForm.patchValue({
        note: this.addErrorLogForm.value.note.trim()
      })
    }
    if (this.addErrorLogForm.invalid) {
      return;
    }

    let content = this.addErrorLogForm.value;
    content.status = this.addErrorLogForm.value.status.id;

    let params = {
      method: "PUT",
      content: content,
    };
    Swal.showLoading();
    this.service
      .editSoftwareLog(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditSoftwareLog.emit('completed');
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
      });

  }

  resetForm(){
    this.fillForm()
  }

    // modal Open Small
    modalOpenSM(modalSM) {
      this.modalService.open(modalSM, {
          centered: true,
          size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
      });
  }

}
