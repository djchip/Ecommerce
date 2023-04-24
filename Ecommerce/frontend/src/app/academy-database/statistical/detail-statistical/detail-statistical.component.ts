import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from './../../../services/change-language.service';
import { StatisticalService } from './../statistical.service';
import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-detail-statistical',
  templateUrl: './detail-statistical.component.html',
  styleUrls: ['./detail-statistical.component.scss']
})
export class DetailStatisticalComponent implements OnInit {

  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  public data;
  public items = [];
  public length;

  constructor(private service: StatisticalService, private _changeLanguageService: ChangeLanguageService, 
    private _translateService: TranslateService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() => {
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.getReport(window.localStorage.getItem('ISR'));
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
          this.items = JSON.parse(response.content.items);
          this.length = this.items.length;
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
