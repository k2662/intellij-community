/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.vcs.log.ui.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.SearchTextFieldWithStoredHistory;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.VcsLogTextFilter;
import com.intellij.vcs.log.impl.VcsLogFilterCollectionImpl;
import com.intellij.vcs.log.ui.VcsLogUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 */
public class VcsLogClassicFilterUi implements VcsLogFilterUi {

  @NotNull private final SearchTextField myTextFilter;
  @NotNull private final VcsLogUI myUi;
  @NotNull private final DefaultActionGroup myActionGroup;

  @NotNull private final BranchFilterPopupComponent myBranchFilterComponent;
  @NotNull private final UserFilterPopupComponent myUserFilterComponent;
  @NotNull private final DateFilterPopupComponent myDateFilterComponent;
  @NotNull private final StructureFilterPopupComponent myStructureFilterComponent;

  public VcsLogClassicFilterUi(@NotNull VcsLogUI ui) {
    myUi = ui;

    myTextFilter = new SearchTextFieldWithStoredHistory("Vcs.Log.Text.Filter.History");
    myTextFilter.getTextEditor().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyFilters();
        myTextFilter.addCurrentTextToHistory();
      }
    });

    myBranchFilterComponent = new BranchFilterPopupComponent(this, ui);
    myUserFilterComponent = new UserFilterPopupComponent(this, ui.getLogDataHolder(), ui.getUiProperties());
    myDateFilterComponent  = new DateFilterPopupComponent(this);
    myStructureFilterComponent = new StructureFilterPopupComponent(this, ui.getLogDataHolder().getRoots());

    myActionGroup = new DefaultActionGroup();
    myActionGroup.add(new TextFilterComponent(myTextFilter));
    myActionGroup.add(new FilterActionComponent(myBranchFilterComponent));
    myActionGroup.add(new FilterActionComponent(myUserFilterComponent));
    myActionGroup.add(new FilterActionComponent(myDateFilterComponent));
    myActionGroup.add(new FilterActionComponent(myStructureFilterComponent));
  }

  @Override
  public ActionGroup getFilterActionComponents() {
    return myActionGroup;
  }

  @NotNull
  @Override
  public VcsLogFilterCollection getFilters() {
    VcsLogTextFilter textFilter = !myTextFilter.getText().isEmpty() ? new VcsLogTextFilterImpl(myTextFilter.getText()) : null;
    return new VcsLogFilterCollectionImpl(myBranchFilterComponent.getFilter(), myUserFilterComponent.getFilter(),
                                          myDateFilterComponent.getFilter(), textFilter, myStructureFilterComponent.getFilter());
  }

  void applyFilters() {
    myUi.applyFiltersAndUpdateUi();
  }


  private static class TextFilterComponent extends DumbAwareAction implements CustomComponentAction {

    private final SearchTextField mySearchField;

    TextFilterComponent(SearchTextField searchField) {
      mySearchField = searchField;
    }

    @Override
    public JComponent createCustomComponent(Presentation presentation) {
      JPanel panel = new JPanel();
      JLabel filterCaption = new JLabel("Filter:");
      filterCaption.setForeground(UIUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getInactiveTextColor());
      panel.add(filterCaption);
      panel.add(mySearchField);
      return panel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }
  }

  private static class FilterActionComponent extends DumbAwareAction implements CustomComponentAction {
    private final FilterPopupComponent myComponent;

    public FilterActionComponent(FilterPopupComponent component) {
      myComponent = component;
    }

    @Override
    public JComponent createCustomComponent(Presentation presentation) {
      return myComponent;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }
  }

}
