import { ChangeLanguageService } from 'app/services/change-language.service';
import { ChangeDetectorRef,Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { CriteriaService } from '../criteria.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TokenStorage } from 'app/services/token-storage.service';
import { DirectoryService } from 'app/exhibition/directory/directory.service';


@Component({
  selector: 'app-edit-criteria',
  templateUrl: './edit-criteria.html',
  styleUrls: ['./edit-criteria.scss']
})
export class EditCriteriaComponent implements OnInit {

  @Output() afterEditDirectory = new EventEmitter<string>();

  public contentHeader: object;
  public addDirectory: FormGroup;
  public addDirectorySubmitted = false;
  public data;
  public id;
  public autoCode;
  public codeSS;
  public codeEE;
  public mergedPwdShow = false;
  public roleLoading = false;
  public chooseFeather = "";
  public listOrg = [];
  public listPrograms = [];
  public listStandard = [];
  public codeEXH = "H";
  public codeSSS = "00";
  public idEXH;
  public currentUserName;
  // public listUser;
  standardSet = null;
  public Code = false;
  public listCat=[];
  // public listRole = [];
  public currentLang = this._translateService.currentLang;
  public roleAdmin = window.localStorage.getItem("ADM");
  constructor(
    private ref: ChangeDetectorRef, 
    private modalService: NgbModal,
    private formBuilder: FormBuilder, 
    private service: CriteriaService, 
    private tokenStorage: TokenStorage, 
    public directoryService: DirectoryService,
    public _translateService: TranslateService, 
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.currentUserName = this.tokenStorage.getUsername();
    this.initForm();
    this.id = window.localStorage.getItem("id");
    this.getLisCategories();
    this.getDirectoryDetail();
    // this.getListUser();
    // this.getListStandard();
    this.getListOrg();
  
  }

  initForm(){
    this.addDirectory = this.formBuilder.group(
      {
        id: ['', [Validators.required]],
        code: ['', [Validators.required]],
        name: ['', [Validators.required]],
        nameEn: [''],
        description: [''],
        descriptionEn: [''],
        userInfos: [null],
        standardId: ['',[Validators.required]],
        organizationId: ['',[Validators.required]],
        categoryId:['',Validators.required],

      },
    );
  }
  onChangeName(){
    let code = this.addDirectory.value.name;
    const codeC = code.split(" ").pop();
    const split = codeC.split(".");
    const codeE = codeC.split(".").pop();
    const codeS = codeC.split(".")[0];
    if (codeE % 1 == 0 && codeS % 1 == 0 && split.length == 2) {
      if (this.Code) {
        if (codeS.length == 1) {
          this.codeSS = "0" + codeS;
          this.codeSSS = "0" + codeS;
        } else {
          this.codeSS = codeS;
          this.codeSSS = codeS;

        }
        if (codeE.length == 1) {
          this.codeEE = "0" + codeE;
        } else {
          this.codeEE = codeE;
        }
      } 
      else {
          this.codeSS = codeS;

          if (codeS.length == 1) {
            this.codeSSS = "0" + codeS;
          } else {
            this.codeSSS = codeS;
          }

        if (codeE.length == 1) {
          this.codeEE = "0" + codeE;
        } else {
          this.codeEE = codeE;
        }
      }
      this.autoCode = this.codeEXH + this.codeSS + "." + this.codeSSS + "." + this.codeEE;
    } else {
      this.autoCode = "";
    }
  }



  getListOrg(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrganization(params)
      .then((data) => {
        Swal.close();
        let response = data;
        // console.log(data.content);
        
        if (response.code === 0) {
          this.listOrg = data.content;
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

  // getListUser(){
  //   let params = {
  //     method: "GET",
  //   };
  //   Swal.showLoading();
  //   this.directoryService
  //     .getListUser(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listUser = data.content;
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //       }
  //     })
  //     .catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }

  getEXH(id: number){
    if(id){
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getCodeEXH(params, id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            // this.codeEXH = data.content;

            this.data = response.content;

            this.codeEXH = this.data.name;
            this.Code = this.data.enCode;

            // console.log(this.Code);
            
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



  fillForm(){
    this.addDirectory.patchValue(
      {
        id: this.data.id,
        code: this.data.code,
        name: this.data.name,
        nameEn: this.data.nameEn,
        description: this.data.description,
        descriptionEn: this.data.descriptionEn,
        organizationId: this.data.organizationId,
        standardId: this.data.standardId,
        userInfos: this.data.userInfos,
        categoryId: this.data.categoryId,
      },
    );
    this.idEXH = this.data.organizationId;
  }

  get AddDirectory(){
    return this.addDirectory.controls;
  }



  async getDirectoryDetail(){
    this.getListStandard();
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .getCriteria(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            // console.log("DATA",this.data.create_by);

            this.getEXH(this.data.organizationId);
            let code = this.data.name;
            // console.log(code);
            const codeC = code.split(" ").pop();
            const split = codeC.split(".");
            const codeE = codeC.split(".").pop();
            const codeS = codeC.split(".")[0];
            if(codeE%1 == 0 && codeS%1 == 0 && split.length == 2){
              if(codeS.length == 1){
                this.codeSS = "0"+ codeS;
              } else {
                this.codeSS = codeS;
              }
              if(codeE.length == 1){
                this.codeEE = "0"+ codeE;
              } else {
                this.codeEE = codeE;
              }
              this.autoCode = this.codeEXH + this.codeSS + "."+ this.codeSS + "." + this.codeEE;
            } else {
              this.autoCode = "";
            }

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

  // getListPrograms(){
  //   let params = {
  //     method: "GET",
  //   };
  //   Swal.showLoading();
  //   this.service
  //     .getListPrograms(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listPrograms = data.content;
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //       }
  //     })
  //     .catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });

  // }

  editDirectory(){
    this.addDirectorySubmitted = true;

    if(this.addDirectory.value.name !== ''){
      this.addDirectory.patchValue({
        name: this.addDirectory.value.name.trim()
      })
    }

    if (this.addDirectory.invalid) {
      return;
    }

    let content = this.addDirectory.value;
    content.status = this.addDirectory.value.status;

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editDirectory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.CRITERIA.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditDirectory.emit('completed');
          });
        }else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.CRITERIA.CRITERIA_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
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
    this.getDirectoryDetail();
  }

    // modal Open Small
    modalOpenSM(modalSM) {
      this.modalService.open(modalSM, {
          centered: true,
          size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
      });
  }

  copy(value) {
      this.addDirectory.patchValue({
          icon: value
      })
      this.chooseFeather = value;
      this.ref.detectChanges();
  }


  // getListStandard(){
  //   let params = {
  //     method: "GET"
  //   };
  //   this.service.getListDirectory(params).then(data=>{
  //     if(data.code == 0){
  //       this.listStandard = JSON.parse(JSON.stringify(data.content));
  //     }
  //   })
  // }
  getListStandard(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListStandard(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listStandard = data.content;
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

  public selectedTreeList = []
  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
  }

  getLisCategories() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getLisCategories(params)
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
