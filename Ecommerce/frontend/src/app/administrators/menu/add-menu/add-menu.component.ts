import {Component, OnInit, ViewEncapsulation, ChangeDetectorRef, Output, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import { MenuService } from '../menu.service';
import Swal from "sweetalert2";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-add-menu',
  templateUrl: './add-menu.component.html',
  styleUrls: ['./add-menu.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AddMenuComponent implements OnInit {

  @Output() afterCreateMenus = new EventEmitter<string>();

  public contentHeader: object;
  public listMenu = [{id:"1", name: "Level 1"}, {id:"2", name: "Level 2"}, {id:"3", name: "Level 3"}];
  public menuForm: FormGroup;
  public addUserFormSubmitted = false;
  public mergedPwdShow = false;
  public json = require('feather-icons/dist/icons.json');
  public data;
  public searchText;
  public chooseFeather = "";
  disableUrl = true;
  sunmitted = true;

  constructor(private formBuilder: FormBuilder,
              private service: MenuService,
              private modalService: NgbModal,
              private ref: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.disableUrl = true;
    this.getListMenu();
    this.data = this.json;
    this.contentHeader = {
      headerTitle: 'Thêm mới Menu',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Trang chủ',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản trị hệ thống',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản lý Menu',
            isLink: false
          }
        ]
      }
    };
    this.menuForm = this.formBuilder.group(
      {
        menuCode: ['',Validators.required],
        menuName: ['',Validators.required],
        url: ['',Validators.required],
        icon: ['',Validators.required],
        menuParentId: ['',Validators.required],
        translate: ['',Validators.required],
      },
    );
  }

  addMenu(){
    this.addUserFormSubmitted = true;

    if (this.menuForm.invalid){
      return;
    }

    let content= this.menuForm.value;
    let parentId = content.menuParentId.id;
    content.menuParentId = parentId;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addMenu(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: "Thêm mới menu thành công.",
          }).then((result) => {
            this.afterCreateMenus.emit('completed');
          });
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: "Ma menu đã tồn tại , vui lòng nhập ma menu khác",
          }).then((result) => {
          });
        }else if(response.code ===100){
          Swal.fire({
            icon: "error",
            title: "Ten menu đã tồn tại , vui lòng nhập ten menu khác",
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
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });

  }

  getListMenu(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListMenuParrentUrl(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listMenu = data.content;
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

  get AddMenuFrom(){
    return this.menuForm.controls;
  }

  // modal Open Small
  modalOpenSM(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copy(value) {
    this.menuForm.patchValue({
      icon: value
    })
    this.chooseFeather = value;
    this.ref.detectChanges();
  }

  onChange(event){
    this.disableUrl = false
  }

  resetCalculations(){
    this.disableUrl = true;
  }

  resetForm(){
    this.ngOnInit();
    this.menuForm.reset();
    this.sunmitted = false;

  }


}
