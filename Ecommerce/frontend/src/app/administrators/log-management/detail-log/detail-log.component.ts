import { TranslateService } from '@ngx-translate/core';
import { LogService } from './../log.service';
import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-detail-log',
  templateUrl: './detail-log.component.html',
  styleUrls: ['./detail-log.component.scss']
})
export class DetailLogComponent implements OnInit {

  public id;
  public data: any;

  constructor(private service: LogService, public _translateService: TranslateService) { }

  ngOnInit(): void {
    this.id = window.localStorage.getItem("id");
    this.getLogDetail();
  }

  getLogDetail(){
    if(this.id !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailLog(params, this.id)
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
}
