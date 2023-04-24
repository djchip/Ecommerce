import { Component, OnInit,Output,EventEmitter,ViewChild, ElementRef } from '@angular/core';
import { ProofManagementService } from 'app/exhibition/proof-management/proof-management.service';

@Component({
  selector: 'app-input-code',
  templateUrl: './input-code.component.html',
  styleUrls: ['./input-code.component.scss']
})
export class InputCodeComponent implements OnInit {

  constructor(private service: ProofManagementService) { }

  public listCode:any;
  public listExhCode = [];
  public listProofCode = [];
  public listCodeExhOfSta = [];
  public listCodeExhOfCri = [];

  @Output() emitEvent = new EventEmitter<string>();

  ngOnInit(): void {
    this.getListExhibitionCode();
    this.getListExhibitionCode();
    this.getListExhibitionCode();
  }

  genCodeForSta(){
    let params = {
      method: "GET",
    }
    this.service.getCodeSta(params)
      .then((data) => {
      let response = data;
      if (response.code === 0) {
        this.listCodeExhOfSta = data.content;
        console.log('code' + 1)
      } else {
      }
    })
  }

  genCodeForCri(){
    let params = {
      method: "GET",
    }
    this.service.getCodeCri(params)
      .then((data) => {
      let response = data;
      if (response.code === 0) {
        this.listCodeExhOfCri = data.content;
        console.log('code' + 2)
      } else {
      }
    })
  }

  genCode(){
    this.genCodeForCri();
    this.genCodeForSta();
  }

  getListExhibitionCode() {
    this.listExhCode = [];
    let standard = window.localStorage.getItem("standard");
    let criteria = window.localStorage.getItem("criteria");
    
    if(standard != '' && criteria == '') 
    {
      this.genCodeForSta();
      this.genCodeForSta();
      this.genCodeForSta();
    } else if(criteria != '' && standard == '') 
    {
      this.genCodeForCri();
      this.genCodeForCri();
      this.genCodeForCri();
    } else {
      this.genCode();
      this.genCode();
      this.genCode();
    }
    
    let params = {
      method: "GET", standard: standard, criteria: criteria
    };
    this.service
      .getExhibitionCode(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listCode = response.content;
          console.log(this.listCodeExhOfSta)

          this.listCodeExhOfSta.forEach(element => {
            this.listExhCode.push(element)
            console.log('ele ' + element);
          });
          this.listCodeExhOfCri.forEach(element => {
            this.listExhCode.push(element)
            console.log('ele ' + element);
          });
        } else {
        }
      })
  }

  saveExhCode(){
    let index = 0;
    this.listCode.forEach(element => {
        element.exhibitionCode = element.exhibitionCode + '.' + this.listExhCode[index];
        index++;
    });
    this.emitEvent.emit(this.listCode)
    console.log(this.listCode)
  }

}
