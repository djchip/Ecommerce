import { AutoImportComponent } from './../auto-import/auto-import.component';
import { ProgramService } from './../programs-management/programs.service';
import { ChangeLanguageService } from './../../services/change-language.service';
import { AutoUploadService } from './../auto-upload/auto-upload.service';
import { AutoUploadComponent } from './../auto-upload/auto-upload/auto-upload.component';
import { DirectoryService } from 'app/exhibition/directory/directory.service';
import { CoreTranslationService } from '@core/services/translation.service';
import Stepper from "bs-stepper";
import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { OrganizationService } from 'app/exhibition/organization/organization.service';

@Component({
  selector: "app-upload-form-wizard",
  templateUrl: "./upload-form-wizard.component.html",
  styleUrls: ["./upload-form-wizard.component.scss"],
  encapsulation: ViewEncapsulation.None
})
export class UploadFormWizardComponent implements OnInit {
  @ViewChild(AutoUploadComponent) child: AutoUploadComponent;
  @ViewChild(AutoImportComponent) autoImport: AutoImportComponent;
  public contentHeader: object;
  private verticalWizardStepper: Stepper;

  private bsStepper;
  public messageNotFound;
  public programId = null;
  public organizationId = null;
  public currentLang = this._translateService.currentLang;
  public data;
  public encodeBy = true;

  constructor(private _coreTranslationService: CoreTranslationService,
    private programService: DirectoryService,
    public _translateService: TranslateService,
    private orgService: OrganizationService,
    public service: AutoUploadService,
    private _serviceProgram: ProgramService,
    private _changeLanguageService: ChangeLanguageService) {
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
        this.setContentHeader();
      })
    }

  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.verticalWizardStepper = new Stepper(document.querySelector('#stepper2'), {
      linear: false,
      animation: true
    });

    this.bsStepper = document.querySelectorAll('.bs-stepper');
    this.setContentHeader();
    this.getPrograms();
    this.getListOrganization();
    this.messageNotFound = this._translateService.instant('MESSAGE.COMMON.MESSAGE_NOT_FOUND');
  }

  setContentHeader(){
    this.contentHeader = {
      headerTitle: this._translateService.instant('MENU.UPLOAD_FORM_WIZARD'),
      actionButton: true,
      breadcrumb: {
        type: "",
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: "/dashboard",
          },
          {
            name: this._translateService.instant('MENU.EXHIBITION'),
            isLink: false,
            link: "/",
          },
          {
            name: this._translateService.instant('MENU.UPLOAD_FORM_WIZARD'),
            isLink: false,
          },
        ],
      },
    };
  }

  /**
   * Vertical Wizard Stepper Next
   */
  verticalWizardNext() {
    this.verticalWizardStepper.next();
  }

  /**
   * Vertical Wizard Stepper Previous
   */
  verticalWizardPrevious() {
    this.verticalWizardStepper.previous();
  }

  /**
   * On Submit
   */
  onSubmit() {
    alert("Submitted!!");
    return false;
  }

  programs=[];
  getPrograms() {
    let params = {
      method: "GET",
    };
    this.programService
      .getListPrograms(params)
      .then((res) => {
        if (res.statusCode == 200) {
          this.programs = res.content;
        }
      }).catch((r) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      }).finally(()=>{
        this.onClear();
      });
  }

  listOrganization=[];
  getListOrganization(){
    let params = {
      method: "GET",
    };
    this._serviceProgram
      .getListOrganization(params)
      .then((data) => {
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
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });
  }

  proofList
  autoUploadExhibition(){
    let params = {
      method: "POST", content: this.programId
    };
    Swal.showLoading();
    this.service
      .autoUploadExhibition(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.proofList = response.content;
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.SUCCESS_MAP'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        } else if(response.code === 2){
          Swal.fire({
            icon: "warning",
            title: this._translateService.instant('MESSAGE.AUTO_UPLOAD_EXHIBITION.NOT_FOUND'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        } 
        else {
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

  onClear() {
    this.child.onClear();
    this.autoImport.onClear();
  }
  
  changeProgram() {
    this.child.getCollectedExhibition(this.programId);
    this.autoImport.getProofHaveNotFile(this.programId);
    // console.log("id ", this.programId);
    // console.log("id encode ", this.encodeBy);
  }


  onChange(orgId) {
    
    this.getProgramsByOrgId(orgId);
    this.getOrd(orgId);
    // console.log("encodeBy ", this.encodeBy);
  }
  getProgramsByOrgId(orgId) {
    if(orgId==null){
      return;
    }
    let params = {
      method: "GET",
      orgId: orgId
    };
    this._serviceProgram
      .findByOrgId(params)
      .then((res) => {
        if (res.statusCode == 200) {
          this.programs = res.content;
        }
      })
      .catch((r) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  getOrd(orgId){
    if (orgId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.orgService
        .findOrgId(params, orgId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.encodeBy = this.data.encode;
            console.log("encodeBy ", this.encodeBy);
          } else {
            this.encodeBy = false;
          }
        })
    }
  }
}
