import { NgModule } from '@angular/core';

import { FeatherIconDirective } from '@core/directives/core-feather-icons/core-feather-icons';
import { RippleEffectDirective } from '@core/directives/core-ripple-effect/core-ripple-effect.directive';
import { HighlightDirective } from './highlight.directives';
import { MyCustomDirectiveDirective } from './my-custom-directive.directive';
import { TextSelectDirective } from './text-select/text-select.directives';

@NgModule({
  declarations: [RippleEffectDirective, FeatherIconDirective, TextSelectDirective, HighlightDirective, MyCustomDirectiveDirective],
  exports: [RippleEffectDirective, FeatherIconDirective, TextSelectDirective, HighlightDirective, MyCustomDirectiveDirective]
})
export class CoreDirectivesModule { }
