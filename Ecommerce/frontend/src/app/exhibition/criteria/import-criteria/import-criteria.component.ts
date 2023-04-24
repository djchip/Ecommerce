import { ProgramService } from './../../programs-management/programs.service';
import { TranslateService } from '@ngx-translate/core';
import { DirectoryService } from './../../directory/directory.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ChangeDetectionStrategy, Component, OnInit, Output, EventEmitter } from '@angular/core';
import Swal from 'sweetalert2';
import { FileUploader } from 'ng2-file-upload';
import { HttpParams } from '@angular/common/http';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-import-criteria',
  templateUrl: './import-criteria.component.html',
  styleUrls: ['./import-criteria.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImportCriteriaComponent implements OnInit {

  @Output() reloadFolder = new EventEmitter<string>();

  public importFG: FormGroup;
  listStandard=[];
  ImportSubmitted = false;
  public uploader: FileUploader;
  public listCat=[];
  disableCat = true;

  public currentLang = this._translateService.currentLang;

  constructor(    
    private programService: DirectoryService,
    private formBuilder: FormBuilder,
    private service: DirectoryService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService,
    private _serviceProgram: ProgramService,
    private _coreTranslationService: CoreTranslationService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
        // this.searchDirectory();
      })
    }

  ngOnInit(): void {
    this.uploader = new FileUploader({
      isHTML5: true
    });
    this.autoFormG();
    // this.getLisCategories();
    this.getListOrganization();
    this._coreTranslationService.translate(eng, vie);
  }

  autoFormG() {
    this.importFG = this.formBuilder.group({
      organizationId: [null, [Validators.required]],
      categoryId:[null,Validators.required],

    });
  }

  get ImportFG() {
    return this.importFG.controls;
  }
  resetCat() {
    this.disableCat = true;
  }

  listOrganization=[];
  getListOrganization(){
    let params = {
      method: "GET",
    };
    this._serviceProgram
      .getListOrganizationForCriteria(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrganization = data.content;
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

  async import(i) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.WARNING_OVERRIDE'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if(result.value){
        if(!this.validate()){
          return;
        };
        Swal.fire(this._translateService.instant('MESSAGE.COMMON.UPLOADING_DATA'))
        Swal.showLoading();
        var index = 0;
        this.uploader.queue.forEach(element => {
          if(index !== i){
            index++;
            return;
          }
          this.programService.importCriteria( element._file, { content: { organizationId: this.importFG.value.organizationId , categoryId: this.importFG.value.categoryId } }).then((data) => {
            Swal.close();
            if (data.body.code === 0) {
              element.isUploaded = true;
              element.isSuccess = true;
              element.progress = 100;
              Swal.fire({
                icon: 'success',
                title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.SUCCESS_IMPORT'),
                confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
              })
            } else if(data.body.code == 7){
              Swal.fire(this._translateService.instant('MESSAGE.COMMON.NOT_EXCEL_FILE'));
            } else if(data.body.code == 3){
                  Swal.fire(this._translateService.instant('MESSAGE.CRITERIA.ALREADY_EXITS'));
            } else if(data.body.code == 604){
              Swal.fire(this._translateService.instant('MESSAGE.CRITERIA.ALREADY_EXITS'));
            }
           else if(data.body.code == 56){
            Swal.fire(this._translateService.instant('MESSAGE.CRITERIA.ALREADY_EXITS1'));
          }
          
           else {
              element.isError = true;
              element.progress = 100;
            }
            if (element == this.uploader.queue[this.uploader.queue.length - 1]) {
              this.uploader.progress = 100;
              this.reloadFolder.emit();
            }
          }).catch(e => {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant(
                "MESSAGE.AUTO_UPLOAD_EXHIBITION.WRONG_FORMAT"
              ),
              confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
            });
            
            // Swal.fire(this._translateService.instant('MESSAGE.COMMON.SWW'));
          })
          index = 0;
        })
        this.ImportSubmitted = false;
      }
    });
  }

  validate() :Boolean {
    this.ImportSubmitted = true;
    return this.importFG.invalid ? false : true;
  }

  downloadMyFile(){
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/template-excel/Mau tieu chi.xlsx');
    link.setAttribute('download', `Mau tieu chi.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  clear() {
    this.uploader.clearQueue();
  }



  onChangee() {
    this.disableCat = false;
    
    if (this.importFG.value.organizationId != null) {
      let params = {
        method: "GET",
        OrgId: this.importFG.value.organizationId,
      };
      Swal.showLoading();
      this.service
        .getLisCategorie(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.listCat = data.content;
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
}