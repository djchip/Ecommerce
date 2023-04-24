import { Component, Input, OnInit } from '@angular/core';
import { StatisticalService } from '../statistical.service';
import Swal from 'sweetalert2';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-export-statistical',
  templateUrl: './export-statistical.component.html',
  styleUrls: ['./export-statistical.component.scss']
})
export class ExportStatisticalComponent implements OnInit {

  @Input() select;
  public dateFormat = window.localStorage.getItem("dateFormat");

  constructor(private service: StatisticalService) { }

  ngOnInit(): void {
    console.log("Export: ", this.select);
  }

  deleteItem(id) {
    for (let i = 0; i < this.select.length; i++) {
      if (this.select.indexOf(this.select[i]) === id) {
        this.select.splice(i, 1);
        break;
      }
    }
  }

  exportForm(){
    Swal.showLoading();
    for (let i = 0; i < this.select.length; i++) {
      let date = new Date();
      let fileName = "Biểu mẫu_" + this.select[i]["name"] + "_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
      this.service.exportForm(fileName, this.select[i]["id"]).then((data) => {
        if(i === this.select.length - 1){
          Swal.close();
        }
      }).catch((error) => {
        Swal.close();
      });
    }
  }

  exportReport(){
    for (let i = 0; i < this.select.length; i++) {
      Swal.showLoading();
      let date = new Date();
      let fileName = this.select[i]["name"] + "_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
      this.service.export(fileName, this.select[i]["id"]).then((data) => {
        if(i === this.select.length - 1){
          Swal.close();
        }
      }).catch((error) => {
        Swal.close();
      });
      
    }
  }

}
