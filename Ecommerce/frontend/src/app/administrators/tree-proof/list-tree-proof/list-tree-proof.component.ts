import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import Swal from "sweetalert2";
import { TreeModel, TreeNode, ITreeState, ITreeOptions } from '@circlon/angular-tree-component';
import { TranslateService } from '@ngx-translate/core';
import { TreeProofService } from '../../tree-proof/tree-proof.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { v4 } from 'uuid';

@Component({
  selector: 'app-list-tree-proof',
  templateUrl: './list-tree-proof.component.html',
  styleUrls: ['./list-tree-proof.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListTreeProofComponent implements OnInit {
  public selectedTreeList = []
  public contentHeader: object;
  public listStar = [];
  public isInvalidRole = true;
  public role;

  public selectedStaList = []
  public currentLang = this._translateService.currentLang;
  public nodesFilter = [];
  public id;
  public idPro = "";
  public listPrograms = [];
  // public addProofForm: FormGroup;
  public list = [];
  public listCode = "";
  public roleAdmin = window.localStorage.getItem("ADM");


  stateDragDrop: ITreeState = {
    expandedNodeIds: {
      1: true,
      2: true
    },
    hiddenNodeIds: {},
    activeNodeIds: {}
  };
  optionsDragDrop: ITreeOptions = {
    allowDrag: (node) => node.isLeaf,
    getNodeClone: (node) => ({
      ...node.data,
      id: v4(),
      name: `copy of ${node.data.name}`
    })
  };

  constructor(
    // private formBuilder: FormBuilder,
    private _coreTranslationService: CoreTranslationService,
    private service: TreeProofService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService,
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    // this.searchSoftwareLog();
    this.contentHeader = {
      headerTitle: 'MENU.PROOFTREE',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'CONTENT_HEADER.EXHIBITION',
            isLink: false,
            link: '/'
          },
          {
            name: 'MENU.PROOFTREE',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.getListPrograms();

  }
  // initForm() {
  //   this.addProofForm = this.formBuilder.group(
  //     {
  //       programId: [null],
  //     }
  //   )
  // }

  // get AddProofForm() {
  //   return this.addProofForm.controls;
  // }

  getListPrograms() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListPrograms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listPrograms = data.content;
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

  onChange() {
    console.log("id ", this.idPro)
    // console.log("id ", this.addProofForm.value)

    // this.getListPrograms();
    this.getTreeByProgram();
    this.list = [];
    this.listCode = "";
    window.localStorage.setItem("id Program", this.idPro);

    
  }

  /**
  * filterFn
  *
  * @param value
  * @param treeModel
  */
  // filterFn(value: string, treeModel: TreeModel) {
  //   treeModel.filterNodes((node: TreeNode) => fuzzysearch(value, node.data.name));
  // }
  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);

      console.log('Selected tc ' + this.selectedTreeList);
  }

  // filter
  public optionsFilter = {
    useCheckbox: false
  };

  getTreeByProgram() {
    if (this.idPro) {
      let params = {
        method: "GET"
      };
      this.service
        .getTree(params, this.idPro)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.nodesFilter = response.content;
            console.log("data =", this.nodesFilter)
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

  doSomeThing(){
    console.log("is FILE!!!");
  }
  openTreeproof(id){
    let xMax = window.outerWidth;
      let yMax = window.outerHeight;
      window.open( this.service.getURLEditor(id), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
  }

  onInitTree(event, model) {
    console.log("oke");
    
    console.log(typeof(event) + "BBB")
    this.isInvalidRole = false;
    if (event == undefined) {
      this.role = 0;
      this.selectedTreeList.forEach(element => {
        model.getNodeById(element).setIsSelected(false);
      });
      this.selectedTreeList = [];
      return
    }
    model.expandAll();
    this.selectedTreeList.forEach(element => {
      model.getNodeById(element).setIsSelected(false);
    });


 
  }

}
