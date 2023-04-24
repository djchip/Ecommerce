import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[myCustomDirective]'
})
export class MyCustomDirectiveDirective {
  constructor(private elRef: ElementRef) { }

  ngAfterViewInit() {
    console.log(this.elRef.nativeElement.textContent);
  }

}
