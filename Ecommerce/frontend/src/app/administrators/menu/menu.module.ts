import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddMenuComponent } from './add-menu/add-menu.component';
import { ListMenuComponent } from './list-menu/list-menu.component';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import {EditMenuComponent} from "./edit-menu/edit-menu.component";
import {DetailMenuGroupComponent} from "./detail-menu/detail-menu-group.component";

import { TranslateModule } from '@ngx-translate/core';
const routes: Routes = [
  {
    path: 'add-menu',
    component: AddMenuComponent,
    data: { animation: 'feather' }
  },
  {
    path: 'list-menu',
    component: ListMenuComponent,
    data: { animation: 'feather' }
  },
  {
    path: 'edit-menu',
    component: EditMenuComponent,
    data: { animation: 'feather' }
  },
  {
    path: 'detail-menu',
    component: DetailMenuGroupComponent,
    data: { animation: 'feather' }
  },
];

@NgModule({
    declarations: [
        AddMenuComponent,
        ListMenuComponent,
        EditMenuComponent,
        DetailMenuGroupComponent,
    ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    CorePipesModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    TranslateModule
  ]
})
export class MenuModule { }
